import java.util.List;
import java.util.Map;

public class Yard {
    private Map<Container, List<Slot>> containerSlots;

    public Yard(Map<Container, List<Slot>> containerSlots) {
        this.containerSlots = containerSlots;
    }
}
