// "Make variable mutable" "true"
open class A {
    open var x = 42;
}

class B : A() {
    override var x: Int = 3;
}