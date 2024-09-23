import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import com.kizitonwose.calendar.compose.weekcalendar.StickyHeaderList
import com.kizitonwose.calendar.compose.weekcalendar.rememberWeekCalendarState
import com.kizitonwose.calendar.core.Week
import com.kizitonwose.calendar.core.minusDays
import com.kizitonwose.calendar.core.now
import com.kizitonwose.calendar.core.plusDays
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format.Padding
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.ui.tooling.preview.Preview

data class Event(
    val title: String,
    val location: String,
    val startDateTime: LocalDateTime,
    val endDateTime: LocalDateTime
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun Example12Page(close: () -> Unit = {}) {
    val listState = rememberLazyListState()
    val items = ('A'..'Z').map { it.toString() }
    val events = mapOf(
        "A" to listOf("Alex", "Antonio"),
        "B" to listOf(), // B no tiene eventos
        // ... agrega eventos para cada letra
        "Z" to listOf("Zoe", "Zacarías")
    )

    StickyHeaderList(
        state = listState,
        modifier = Modifier,
        headerMatcher = { itemInfo ->
            itemInfo.key == null
        },
        stickyHeader = {
            val headerItem = listState.layoutInfo.visibleItemsInfo.firstOrNull {
                it.offset <= 0
            }
            if (headerItem != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.LightGray)
                ) {
                    Text("Header: ${items[headerItem.index]}")
                }
            }
        }
    ) {
        LazyColumn(state = listState) {
            items(items) { item ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(if (items.indexOf(item) % 2 == 0) Color.White else Color.Yellow)
                        .padding(8.dp)
                ) {
                    Text(item)
                    val itemEvents = events[item]
                    if (itemEvents != null && itemEvents.isNotEmpty()) {
                        itemEvents.forEach { event ->
                            Text("- $event")
                        }
                    } else {
                        Text("- No hay ejemplos")
                    }
                }
            }
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
private fun DayViewItem(date: LocalDate, event: Any?, onDayClick: (LocalDate) -> Unit? = {}) {
    Card(
        onClick = { onDayClick(date) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {

            if (event != null) {
                EventCard(event = (event as Event))
            } else {
                Text("No event for this date ${date.toString()}")
            }
        }
    }
}
//@Composable
//private fun DayViewItem(date: LocalDate, events: List<Event>, onDayClick: (LocalDate) -> Unit) {
//    Card(
//        onClick = { onDayClick(date) },
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(8.dp),
//    ) {
//        Column(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(8.dp)
//        ) {
//            //Este texto hacerlo sticky
//            Text(
//                text = date.toString(),
//                fontWeight = FontWeight.Bold,
//                modifier = Modifier.padding(bottom = 8.dp)
//            )
//            if (events.isNotEmpty()) {
//                events.forEach { event ->
//                    EventCard(event = event)
//                }
//            } else {
//              Text("No event for this date")
//            }
//        }
//    }
//}

@Composable
fun EventCard(event: Event) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = event.title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "From: ${event.startDateTime}")
            Text(text = "To: ${event.endDateTime}")
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Location: ${event.location}")
        }
    }
}

@Preview
@Composable
private fun Example12Preview() {
    Example12Page()
}
