package part2.ndbckend.learn.exeption

import java.lang.RuntimeException

class ResourceNotFoundException (message: String): RuntimeException(message)