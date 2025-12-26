package part2.ndbckend.learn.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "books")
class Book (
    @Id
    val id: String,
    @Column(name = "judul")
    var judul: String,
    @Column(name = "tahun_terbit")
    var tahunTerbit: String,
    @Column(name = "penulis")
    var penulis: String
)