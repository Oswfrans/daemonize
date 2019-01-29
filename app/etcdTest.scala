import org.etcd4s.{Etcd4sClientConfig, Etcd4sClient}
import org.etcd4s.implicits._
import org.etcd4s.formats.Formats._
import org.etcd4s.pb.etcdserverpb._

import scala.concurrent.ExecutionContext.Implicits.global

// create the client
val config = Etcd4sClientConfig(
  address = "127.0.0.1",
  port = 2379
)
val client = Etcd4sClient.newClient(config)

//if key
val hello = "0000"
val currentKey = client.kvService.getKey(hello)
if ( currentKey != null )  {
  println(currentKey)
  client.kvService.setKey(hello, currentKey + 1)
}
else {
  println(0)
  client.kvService.setKey(hello, 1)
}

// remember to shutdown the client
client.shutdown()

/***
// set a key
client.kvService.setKey("foo", "bar") // return a Future

// get a key
client.kvService.getKey("foo").foreach { result =>
  assert(result == Some("bar"))
}

// delete a key
client.kvService.deleteKey("foo").foreach { result =>
  assert(result == 1)
}

// set more key
client.kvService.setKey("foo/bar", "Hello")
client.kvService.setKey("foo/baz", "World")

// get keys with range
client.kvService.getRange("foo/").foreach { result =>
  assert(result.count == 2)
}

// remember to shutdown the client
client.shutdown()
  */