package xyz.devvydont.smprpg.gui.base

import org.bukkit.Sound
import org.bukkit.entity.Player

/**
 * A class which holds the sound effects playable by a menu.
 */
class MenuSoundManager internal constructor(private val player: Player) {
    private var menuOpen: MenuSound? = null
    private var menuClose: MenuSound? = null
    private var menuOpenSub: MenuSound? = null
    private var menuOpenParent: MenuSound? = null
    private var pageNext: MenuSound? = null
    private var pagePrevious: MenuSound? = null
    private var actionConfirm: MenuSound? = null
    private var actionError: MenuSound? = null

    init {
        this.setMenuOpen(Sound.BLOCK_AMETHYST_BLOCK_PLACE, 0.5f, 1f)
        this.setMenuClose(Sound.BLOCK_AMETHYST_BLOCK_BREAK, 0.3f, 1f)
        this.setMenuOpenSub(Sound.UI_TOAST_IN, 4f, 1f)
        this.setMenuOpenParent(Sound.UI_TOAST_OUT, 4f, 1f)
        this.setPageNext(Sound.ITEM_BOOK_PAGE_TURN, 0.8f, 1f)
        this.setPagePrevious(Sound.ITEM_BOOK_PAGE_TURN, 0.6f, 0.8f)
        this.setActionConfirm(Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.4f, 1f)
        this.setActionError(Sound.ENTITY_ITEM_BREAK, 0.4f, 1f)
    }

    /**
     * Gets the sound that should be played when the menu is initially opened.
     */
    fun playMenuOpen() {
        this.menuOpen?.play()
    }

    /**
     * Sets the sound that should be played when the menu is initially opened.
     */
    fun setMenuOpen(sound: Sound, volume: Float, pitch: Float) {
        this.menuOpen = MenuSound(this.player, sound, volume, pitch)
    }

    /**
     * Gets the sound that should be played when the menu is fully closed.
     */
    fun playMenuClose() {
        this.menuClose?.play()
    }

    /**
     * Sets the sound that should be played when the menu is fully closed.
     */
    fun setMenuClose(sound: Sound, volume: Float, pitch: Float) {
        this.menuClose = MenuSound(this.player, sound, volume, pitch)
    }

    /**
     * Gets the sound that should be played when a sub menu is opened.
     */
    fun playMenuOpenSub() {
        this.menuOpenSub?.play()
    }

    /**
     * Sets the sound that should be played when a sub menu is opened.
     */
    fun setMenuOpenSub(sound: Sound, volume: Float, pitch: Float) {
        this.menuOpenSub = MenuSound(this.player, sound, volume, pitch)
    }

    /**
     * Gets the sound that should be played when a sub menu returns back to its parent menu.
     */
    fun playMenuOpenParent() {
        this.menuOpenParent?.play()
    }

    /**
     * Sets the sound that should be played when a sub menu returns back to its parent menu.
     */
    fun setMenuOpenParent(sound: Sound, volume: Float, pitch: Float) {
        this.menuOpenParent = MenuSound(this.player, sound, volume, pitch)
    }

    /**
     * Gets the sound that should be played when is a menu is paginated forward.
     */
    fun playPageNext() {
        this.pageNext?.play()
    }

    /**
     * Sets the sound that should be played when is a menu is paginated forward.
     */
    fun setPageNext(sound: Sound, volume: Float, pitch: Float) {
        this.pageNext = MenuSound(this.player, sound, volume, pitch)
    }

    /**
     * Gets the sound that should be played when is a menu is paginated backwards.
     */
    fun playPagePrevious() {
        this.pagePrevious?.play()
    }

    /**
     * Sets the sound that should be played when is a menu is paginated backwards.
     */
    fun setPagePrevious(sound: Sound, volume: Float, pitch: Float) {
        this.pagePrevious = MenuSound(this.player, sound, volume, pitch)
    }

    /**
     * Gets the sound that should be played when an action the player did is confirmed.
     */
    fun playActionConfirm() {
        this.actionConfirm?.play()
    }

    /**
     * Sets the sound that should be played when an action the player did is confirmed.
     */
    fun setActionConfirm(sound: Sound, volume: Float, pitch: Float) {
        this.actionConfirm = MenuSound(this.player, sound, volume, pitch)
    }

    /**
     * Gets the sound that should be played when an action the player was invalid.
     */
    fun playActionError() {
        this.actionError?.play()
    }

    /**
     * Sets the sound that should be played when an action the player was invalid.
     */
    fun setActionError(sound: Sound, volume: Float, pitch: Float) {
        this.actionError = MenuSound(this.player, sound, volume, pitch)
    }
}
