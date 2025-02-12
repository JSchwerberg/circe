package io.circe.generic.simple.decoding

import io.circe.{ Decoder, HCursor }
import shapeless.LabelledGeneric

abstract class DerivedDecoder[A] extends Decoder[A]

final object DerivedDecoder extends IncompleteDerivedDecoders {
  implicit def deriveDecoder[A, R](
    implicit
    gen: LabelledGeneric.Aux[A, R],
    decode: => ReprDecoder[R]
  ): DerivedDecoder[A] = new DerivedDecoder[A] {
    final def apply(c: HCursor): Decoder.Result[A] = decode(c) match {
      case Right(r)    => Right(gen.from(r))
      case l @ Left(_) => l.asInstanceOf[Decoder.Result[A]]
    }
    override def decodeAccumulating(c: HCursor): Decoder.AccumulatingResult[A] =
      decode.decodeAccumulating(c).map(gen.from)
  }
}
