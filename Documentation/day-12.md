# Input Validation

Proses memeriksa dan memastikan data yang masuk (input) ke sistem sudah benar, aman, dan sesuai aturan sebelum diproses lebih lanjut atau disimpan ke database.

## Macam-macam Annotation Validation

| Annotation        | Digunakan Untuk                    | Contoh Pemakaian |
|------------------|------------------------------------|------------------|
| @NotNull         | Tidak boleh null                   | val age: Int |
| @NotEmpty       | Tidak boleh null & tidak kosong    | val roles: List<String> |
| @NotBlank       | Tidak boleh null & string kosong   | val username: String |
| @Size           | Panjang min / max                  | @Size(min=8) |
| @Email          | Validasi format email              | val email: String |
| @Min            | Nilai minimum                      | @Min(1) |
| @Max            | Nilai maksimum                     | @Max(100) |
| @Positive       | Harus lebih dari 0                 | val price: Int |
| @PositiveOrZero | >= 0                               | val stock: Int |
| @Negative       | Kurang dari 0                      | val delta: Int |
| @Pattern        | Validasi regex                     | @Pattern(regexp="^[A-Z]+$") |
| @Past           | Tanggal masa lalu                  | val birthDate: LocalDate |
| @PastOrPresent  | Masa lalu / sekarang               | val createdAt: LocalDate |
| @Future         | Tanggal masa depan                 | val expiredAt: LocalDate |
| @FutureOrPresent| Masa depan / sekarang              | val scheduleAt: LocalDate |

## Custom Input Validation

### Annotation Class
```kotlin
@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [UniqueUsernameValidator::class])
annotation class UniqueUsername(
    val message: String = "Username sudah digunakan",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)
```
- `@Target(AnnotationTarget.FIELD)` agar hanya boleh di properti/field
- `@Retention(AnnotationRetention.RUNTIME)` annotation tersedia saat aplikasi berjalan
- `@Constraint(validatedBy = [UniqueUsernameValidator::class])` menandai bahwa annotation memiliki aturan validasi sendiri 
- Parameter `message` untuk memberi pesan error jika validasi gagal
- Parameter `groups` untuk validasi bertahap (group validation)
- Parameter `payload` metadata tambahan untuk constraint, hampir tidak pernah dipakai

### Annotation Constraint
```kotlin
@Component
class UniqueUsernameValidator (
    private val userRepository: UserRepository
): ConstraintValidator<UniqueUsername, String> {
    override fun isValid(p0: String?, p1: ConstraintValidatorContext?): Boolean {

        if (p0.isNullOrBlank()) return true

        return !userRepository.existsByUsername(p0)
    }
}
```
- Berfungsi sebagai constraint dari annotation class yang berisi aturan validasi
- `@Component` menjadikan validator sebagai Spring Bean, karena validator butuh dependency injection
- `ConstraintValidator<UniqueUsername, String>` Menghubungkan annotation dengan tipe data
- `if (p0.isNullOrBlank()) return true` validator tidak bertanggung jawab jika input null
- `return !userRepository.existsByUsername(p0)` validasi username

### Implementasi
```kotlin
class AuthRequest (
    @field:NotBlank
    @field:UniqueUsername
    @Size(min = 5, max = 12)
    val username: String,

    @field:NotBlank
    @Size(min= 5, max = 12)
    val password: String,

    @field:NotNull
    val role: Role
)
```


