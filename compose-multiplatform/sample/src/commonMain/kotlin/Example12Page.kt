import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kizitonwose.calendar.compose.VerticalWeekCalendar
import com.kizitonwose.calendar.compose.WeekCalendar
import com.kizitonwose.calendar.compose.weekcalendar.rememberWeekCalendarState
import com.kizitonwose.calendar.core.minusDays
import com.kizitonwose.calendar.core.now
import com.kizitonwose.calendar.core.plusDays
import kotlinx.datetime.LocalDate
import kotlinx.datetime.format.Padding
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Example12Page(close: () -> Unit = {}) {
    val currentDate = remember { LocalDate.now() }
    val startDate = remember { currentDate.minusDays(500) }
    val endDate = remember { currentDate.plusDays(500) }
    var selection by remember { mutableStateOf(currentDate) }
    val stateVertical = rememberWeekCalendarState(
        startDate = startDate,
        endDate = endDate,
        firstVisibleWeekDate = currentDate,
    )
    val stateHorizontal = rememberWeekCalendarState(
        startDate = startDate,
        endDate = endDate,
        firstVisibleWeekDate = currentDate,
    )

    LaunchedEffect(key1 = stateVertical.firstVisibleWeek) {
        stateHorizontal.scrollToWeek(stateVertical.firstVisibleWeek.days[0].date)
    }

    LaunchedEffect(key1 = stateHorizontal.firstVisibleWeek) {
        stateVertical.scrollToWeek(stateHorizontal.firstVisibleWeek.days[0].date)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
    ) {
        val visibleWeek = rememberFirstVisibleWeekAfterScroll(stateVertical)
        ExampleToolbar(
            title = getWeekPageTitle(visibleWeek),
            navigationIcon = { NavigationIcon(onBackClick = close) },
        )
        Column(modifier = Modifier.fillMaxSize()) {
            BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                WeekCalendar(
                    modifier = Modifier.background(color = Colors.primary),
                    state = stateHorizontal,
                    dayContent = { day ->
                        Day(day.date, isSelected = selection == day.date) { clicked ->
                            if (selection != clicked) {
                                selection = clicked
                            }
                        }
                    },
                )
            }

            VerticalWeekCalendar(
                state = stateVertical,
                dayContent = { day ->
                    DayViewItem(
                        date = day.date,
                        selected = (selection == day.date),
                    ) { clicked ->
                        if (selection != clicked) {
                            selection = clicked
                        }
                    }
                },
            )
        }

    }
}

private val dateFormatter by lazy {
    LocalDate.Format {
        dayOfMonth(Padding.ZERO)
    }
}

@Composable
private fun Day(date: LocalDate, isSelected: Boolean, onClick: (LocalDate) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clickable { onClick(date) },
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier.padding(vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(
                text = date.dayOfWeek.displayText(),
                fontSize = 12.sp,
                color = Color.White,
                fontWeight = FontWeight.Light,
            )
            Text(
                text = dateFormatter.format(date),
                fontSize = 14.sp,
                color = if (isSelected) Colors.example7Yellow else Color.White,
                fontWeight = FontWeight.Bold,
            )
        }
        if (isSelected) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(5.dp)
                    .background(Colors.example7Yellow)
                    .align(Alignment.BottomCenter),
            )
        }
    }
}

@Composable
private fun DayViewItem(date: LocalDate, selected: Boolean, onDayClick: (LocalDate) -> Unit) {
    Card(
        onClick = { onDayClick(date) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .padding(8.dp),
            contentAlignment = Alignment.CenterStart,
        ) {
            Text(text = date.toString())
        }
    }
}

@Preview
@Composable
private fun Example12Preview() {
    Example12Page()
}
