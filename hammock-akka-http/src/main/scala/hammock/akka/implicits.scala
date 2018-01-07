package hammock
package akka

import _root_.akka.http.scaladsl.marshalling._
import _root_.akka.http.scaladsl.unmarshalling._
import _root_.akka.http.scaladsl.model.HttpEntity

trait Implicits {
  implicit def encoderToEntityMarshaller[A: Encoder]: ToEntityMarshaller[A] = Marshaller.strict { a =>
    Encoder[A].encode(a) match {
      case Entity.StringEntity(body, _)    => Marshalling.Opaque(() => HttpEntity(body))
      case Entity.ByteArrayEntity(body, _) => Marshalling.Opaque(() => HttpEntity(body))
    }
  }

  implicit def stringDecoderFromEntityUnmarshaller(implicit D: Decoder[String]): FromEntityUnmarshaller[String] =
    PredefinedFromEntityUnmarshallers.stringUnmarshaller
      .map(str => D.decode(Entity.StringEntity(str)))
      .map(_.fold(throw _, identity))

  implicit def byteArrayDecoderFromEntityUnmarshaller(
      implicit D: Decoder[Array[Byte]]): FromEntityUnmarshaller[Array[Byte]] =
    PredefinedFromEntityUnmarshallers.byteArrayUnmarshaller
      .map(str => D.decode(Entity.ByteArrayEntity(str)))
      .map(_.fold(throw _, identity))

}

object implicits extends Implicits
