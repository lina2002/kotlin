// JET-81 Assertion fails when processing self-referring anonymous objects

val y = object {
  val a = <error>y</error>
}

val z = y.<error>a</error>;

object A {
  val x = A
}

val a = object {
  {
    <error>b</error> <error>+</error> 1
  }
  val x = <error>b</error>
  val y = 1
}

val b = <error>a</error>.<error>x</error>
val c = a.y
