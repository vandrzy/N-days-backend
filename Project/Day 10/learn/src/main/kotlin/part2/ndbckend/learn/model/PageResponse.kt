package part2.ndbckend.learn.model

class PageResponse <T>(
    val data: List<T>,
    val page: Int,
    val size: Int,
    val totalElements: Long,
    val totalPage: Int
)