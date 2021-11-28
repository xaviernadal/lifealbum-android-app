package xaviernadalreales.com.lifealbum.listeners


interface GenericListener<E> {
    fun onElementClicked(element : E, position: Int)
}