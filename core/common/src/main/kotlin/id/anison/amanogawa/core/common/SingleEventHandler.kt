package id.anison.amanogawa.core.common



class SingleEventHandler(private val debounceTime: Long = 500) {
    private var lastTime = 0L

    fun canTrigger(): Boolean {
        val now = System.currentTimeMillis()
        return if (now - lastTime >= debounceTime) {
            lastTime = now
            true
        } else {
            false
        }
    }
}
