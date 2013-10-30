import java.io.Serializable;

import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

public class ValidCoffee implements Serializable{
	
	private final boolean sugar; // yes please
	private final boolean cream;
	
	public ValidCoffee(boolean cream, boolean sugar) {
		this.cream = cream;
		this.sugar = sugar;
	}
}