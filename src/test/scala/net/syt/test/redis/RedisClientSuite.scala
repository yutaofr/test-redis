package net.syt.test.redis

import java.io.ByteArrayOutputStream
import java.util.{Calendar, Date}

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.{DefaultScalaModule, ScalaObjectMapper}
import com.redis.RedisClientPool
import com.redis.serialization.{Format, Parse}
import net.syt.test.redis.RedisClientSuite.{MyRedisClientPool, itemFormat, itemParser}
import org.scalatest.BeforeAndAfterAll
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class RedisClientSuite extends AnyFunSuite with Matchers with BeforeAndAfterAll {

  test("connexion to Redis instance") {
    MyRedisClientPool.withClient(client => {
      client.connect should be (true)
    })
  }

  test("set and get a KV record") {
    MyRedisClientPool.withClient(client => {
      client.set("test:key1", "value1")
      client.get("test:key1") should be ('defined)
      client.get("test:key1").get should be ("value1")
    })
  }

  test("rpush and get all list record") {
    MyRedisClientPool.withClient(client => {

      val values = Array(
        Item(1, Date.from(Calendar.getInstance().toInstant), "Item1"),
        Item(2, Date.from(Calendar.getInstance().toInstant), "Item2")
      )

      client.rpush("test:key2", values.head, values.apply(1))(itemFormat)
      val listLen = client.llen("test:key2")

      listLen should be ('defined)
      listLen.get should be (2)

      val actuallists = client.lrange("test:key2", 0, listLen.get.intValue())(itemFormat, itemParser)
      actuallists should be ('defined)

      actuallists.get should contain theSameElementsAs (values.map(Some(_)))


    })
  }

  override protected def afterAll(): Unit = {

    MyRedisClientPool.withClient(client => {
      client.del("test:key1", "test:key2")
    })

    MyRedisClientPool.close()

    super.afterAll()
  }
}

case class Item(id: Int, ts: Date, payload: String)

object RedisClientSuite {
  val MyRedisClientPool = new RedisClientPool("localhost", 6379)


  lazy val mapper = {
    val mapper = new ObjectMapper() with ScalaObjectMapper
    mapper.registerModule(DefaultScalaModule)
    mapper
  }

  implicit val itemParser = Parse[Item](bytes => {
    mapper.readValue[Item](bytes)
  })

  implicit val itemFormat = new Format(new PartialFunction[Any, Any] {
    override def isDefinedAt(x: Any): Boolean = {
      x match {
        case x: Item => true
        case _ => false
      }
    }

    override def apply(v1: Any): Array[Byte] = {
      val out = new ByteArrayOutputStream()
      mapper.writeValue(out, v1)
      out.toByteArray
    }
  })

}
