import androidx.compose.foundation.MutatePriority
import androidx.compose.foundation.gestures.ScrollScope
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.kizitonwose.calendar.compose.weekcalendar.WeekCalendarState
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.Week
import com.kizitonwose.calendar.core.format.fromIso8601LocalDate
import com.kizitonwose.calendar.core.format.toIso8601String
import com.kizitonwose.calendar.core.now
import com.kizitonwose.calendar.core.plusDays
import com.kizitonwose.calendar.data.DataStore
import com.kizitonwose.calendar.data.VisibleItemState
import com.kizitonwose.calendar.data.getCalendarDaysData
import com.kizitonwose.calendar.data.getWeekCalendarData
import kotlinx.datetime.LocalDate
import kotlinx.datetime.daysUntil

@Composable
public fun rememberDayListState(
    startDate: LocalDate,
    endDate: LocalDate,
    firstVisibleDate: LocalDate): DayListState {
    return rememberSaveable(
        inputs = arrayOf(
            startDate,
            endDate,
            firstVisibleDate
        ),
        saver = DayListState.Saver,
    ) {
        DayListState(
            startDate = startDate,
            endDate = endDate,
            firstVisibleDate = firstVisibleDate,
            visibleItemState = null
        )
    }
}


@Stable
public class DayListState internal constructor(
    startDate: LocalDate,
    endDate: LocalDate,
    firstVisibleDate: LocalDate,
    private val visibleItemState: VisibleItemState? = null
) : ScrollableState {

    private var _firstVisibleDate by mutableStateOf(firstVisibleDate)
    public var firstVisibleDate: LocalDate
        get() = _firstVisibleDate
        set(value) {
            _firstVisibleDate = value
        }
    private var _startDate by mutableStateOf(startDate)
    public var startDate: LocalDate
        get() = _startDate
        set(value) {
            _startDate = value
        }

    private var _endDate by mutableStateOf(endDate)
    public var endDate: LocalDate
        get() = _endDate
        set(value) {
            _endDate = value
        }


    internal val store = getCalendarDaysData(
            desiredStartDate = this.startDate,
            desiredEndDate = this.endDate,
        )

    internal val listState = run {
        val item = visibleItemState ?: run {
            VisibleItemState(firstVisibleItemIndex = getScrollIndex(firstVisibleDate) ?: 0)
        }
        LazyListState(
            firstVisibleItemIndex = item.firstVisibleItemIndex,
            firstVisibleItemScrollOffset = item.firstVisibleItemScrollOffset,
        )
    }

    private fun getScrollIndex(date: LocalDate): Int? {
        if (date !in startDate..endDate) {
            return null
        }
        return startDate.daysUntil(date)
    }

    public suspend fun scrollToDate(date: LocalDate) {
        listState.animateScrollToItem(getScrollIndex(date) ?: return)
    }

    override val isScrollInProgress: Boolean
        get() = listState.isScrollInProgress

    override fun dispatchRawDelta(delta: Float): Float = listState.dispatchRawDelta(delta)

    override suspend fun scroll(
        scrollPriority: MutatePriority, block: suspend ScrollScope.() -> Unit
    ): Unit = listState.scroll(scrollPriority, block)

    public val interactionSource: InteractionSource
        get() = listState.interactionSource

    public companion object {
        internal val Saver: Saver<DayListState, Any> = listSaver(
            save = {
                listOf(
                    it.startDate.toIso8601String(),
                    it.endDate.toIso8601String(),
                    it.firstVisibleDate.toIso8601String(), // Guardar firstVisibleDate
                    it.listState.firstVisibleItemIndex,
                    it.listState.firstVisibleItemScrollOffset,
                )
            },
            restore = {
                DayListState(
                    startDate = (it[0] as String).fromIso8601LocalDate(),
                    endDate = (it[1] as String).fromIso8601LocalDate(),
                    firstVisibleDate = (it[2]as String).fromIso8601LocalDate(), // Restaurar firstVisibleDate
                    visibleItemState = VisibleItemState(
                        firstVisibleItemIndex = it[3] as Int,
                        firstVisibleItemScrollOffset = it[4] as Int,
                    ),
                )
            },
        )
    }
}
