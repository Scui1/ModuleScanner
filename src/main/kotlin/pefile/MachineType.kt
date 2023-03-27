package pefile

enum class MachineType(val value: Int) {
    INTEL386(0x14C),
    AMD64(0x8664);

    companion object {
        fun fromInt(value: Int) = values().firstOrNull() { it.value == value }
    }
}