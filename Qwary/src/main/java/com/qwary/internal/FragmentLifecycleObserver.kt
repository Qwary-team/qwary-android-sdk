package com.qwary.internal

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.qwary.internal.FragmentLifecycleObserver.currentFragment

/**
 * A type alias to make sure the event name is always used for events
 */
@JvmInline
value class EventName(val name: String)

/**
 * A type alias to make sure the fragment name is always used for the fragment
 */
@JvmInline
value class FragmentName(val name: String)

/**
 * Observe changes to the [FragmentManager] to determine if we're still on the same fragment to
 * display a survey
 */
object FragmentLifecycleObserver : FragmentManager.FragmentLifecycleCallbacks() {

    /**
     * The current fragment the user is on
     */
    private var currentFragment: FragmentName? = null

    /**
     * Track the fragment that was showing when the event was reported. This allows us to handle
     * multiple events being tracked across multiple fragments and them coming back in any order
     */
    private val fragmentForEvent = mutableMapOf<EventName, FragmentName>()

    /**
     * Register that we've started tracking an event; keep track of the fragment that was shown
     * when the event was initially tracked
     */
    fun registerEvent(event: String, activity: FragmentActivity) {
        activity.visibleFragmentName?.let { fragment ->
            fragmentForEvent.put(EventName(event), fragment)
        }
    }

    /**
     * We assume that we should be dismissing on page change until the SDK is fully initialized; if
     * once it is initialized it turns out we won't dismiss on page change, we should clear out our
     * tracked data
     */
    fun clearRegisteredEvents() {
        fragmentForEvent.clear()
    }

    /**
     * We consider the same fragment to be showing for an even in three scenarios:
     * 1. There wasn't a fragment associated with the event
     * 2. [currentFragment] is null, which means there hasn't been a fragment going through onResume
     * 3. The same fragment is currently showing as was showing when the event was tracked
     */
    fun sameFragmentVisibleForEvent(event: String) = fragmentForEvent.remove(EventName(event))
        ?.let { eventFragment ->
            currentFragment == null || eventFragment.name == currentFragment?.name
        } ?: true

    override fun onFragmentResumed(fm: FragmentManager, f: Fragment) {
        currentFragment = f.name
        super.onFragmentResumed(fm, f)
    }

    private val Fragment.name: FragmentName
        get() = FragmentName(tag ?: javaClass.name)

    private val FragmentActivity.visibleFragmentName
        get() = supportFragmentManager.fragments.firstOrNull()?.name
}