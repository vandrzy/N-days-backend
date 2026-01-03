# Audit

Mekanisme untuk mencatat siapa user yang melakukan suatu aksi di sistem, serta waktu kapan sebuah data dibuat dan terakhir diubah.

## Pada Database
```sql
CREATE TABLE posts(
   id VARCHAR(150) NOT NULL,
   short_code VARCHAR(150) NOT NULL UNIQUE,
   title VARCHAR(255) NOT NULL,
   description TEXT,
   created_at TIMESTAMP NOT NULL,
   updated_at TIMESTAMP NULL,
   created_by VARCHAR(100),
   updated_by VARCHAR(100)
);
```
Perlu ditambahkan field `created_at`, `updated_at`, `created_by`, `updated_by` pada tabel di database

## Mengaktifkan Fitur Audit pada Spring
```kotlin
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
@SpringBootApplication
class NDaysBackendApplication

fun main(args: Array<String>) {
	runApplication<NDaysBackendApplication>(*args)
}
```
Menambahkan anotasi `@EnableJpaAuditing(auditorAwareRef = "auditorAware")` pada file main aplikasi
- `"auditorAware"` nama bean yang dipakai Spring untuk mengambil user login saat ini.

## Auditor Aware Bean
```kotlin
@Component("auditorAware")
class AuditorAwareImpl: AuditorAware<String> {
    override fun getCurrentAuditor(): Optional<String> {
        val authentication = SecurityContextHolder.getContext().authentication

        if (authentication == null || !authentication.isAuthenticated){
            return Optional.of("system")
        }

        return Optional.of(authentication.name)
    }
}
```
Untuk menentukan user yang login
- Implement `AuditorAware<String>` karena AuditorAware adalah interface dari Spring Data JPA yang tugasnya memberi tahu siapa “auditor” (user) yang sedang melakukan aksi saat entity disimpan atau di-update.
- return `Optional.of("system")` memiliki arti:
  - Tidak ada user login
  - Request public
  - Atau proses internal (scheduler, migration)
- return `Optional.of(authentication.name)` berarti ada user yang login yang akan digunakan untuk field `updated_by`, `created_by`

## Base Audit Entity
```kotlin
@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
abstract class BaseEntity(

    @CreatedDate
    var createdAt: LocalDateTime? = null,

    @LastModifiedDate
    var updatedAt: LocalDateTime? = null,

    @CreatedBy
    @Column(updatable = false)
    var createdBy: String? = null,

    @LastModifiedBy
    var updatedBy: String? = null
)
```
- Berfungsi menyediakan field audit standar
- `@MappedSuperclass` memberitau spring bahwa BaseEntity bukan tabel, field didalamnya diturunkan ke tabel entity anak
- `@EntityListeners(AuditingEntityListener::class)` Mengaktifkan listener JPA, agar audit dapat dijalankan
- `@CreatedDated` Waktu saat data pertama kali dibuat
- `@LastModifiedDated` waktu data terakhir di update
- `@CreatedBy` user yang membuat data pertama kali
- `@LastModifiedBy` user yang melakukan update terakhir kali

## Entity yang Menerapkan Audit
```kotlin
@Entity
@Table(name = "posts")
class Post (
    @Id
    @Column(name = "id")
    val id: String,
    @Column(name = "short_code", unique = true, nullable = false)
    val shortCode: String,
    @Column(name = "title")
    var title: String,
    @Column(name = "description", nullable = true, columnDefinition = "TEXT")
    var description: String?
): BaseEntity()
```
- Melakukan implement `BaseEntity()` untuk mewarisi atribut yang dimiliki
- Pada service atribut yang diwariskan (audit) akan terisi sendiri



