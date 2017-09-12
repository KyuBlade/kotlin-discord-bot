import org.junit.Test

class ExceptionTest {

    @Test
    fun throwExceptionInIf() {
        when (true) {
            true -> doSomething()
        }
    }

    private fun doSomething() {
        try {
            val add = true
            if (add && ExceptionThrower.throwException() == null)
                println("Execute if statement")
            else
                println("Execute else statement")
        } catch (e: Exception) {
            println("Catch exception")
        }
    }
}

object ExceptionThrower {

    fun throwException(): Any? = throw IllegalAccessException()
}