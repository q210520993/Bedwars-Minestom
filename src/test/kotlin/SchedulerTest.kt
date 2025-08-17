import net.minestom.server.timer.ExecutionType
import net.minestom.server.timer.Scheduler
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.util.concurrent.atomic.AtomicBoolean

class SchedulerTest {

    @Test
    fun a() {

        val scheduler = Scheduler.newScheduler()
        val result = AtomicBoolean(false)
        val task = scheduler.scheduleNextTick { result.set(true) }
        Assertions.assertEquals(
            task.executionType(),
            ExecutionType.TICK_START,
            "Tasks default execution type should be tick start"
        )

        Assertions.assertFalse(result.get(), "Tick task should not be executed after scheduling")
        scheduler.process()
        Assertions.assertFalse(result.get(), "Tick task should not be executed after process")
        scheduler.processTickEnd()
        Assertions.assertFalse(result.get(), "Tick task should not be executed after processTickEnd")
        scheduler.processTick()
        Assertions.assertTrue(result.get(), "Tick task must be executed after tick process")
        Assertions.assertFalse(task.isAlive, "Tick task should be cancelled after execution")

    }

}