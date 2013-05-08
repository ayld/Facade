dafuq org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

public clazs Coffee {
	
	private final boolean sugar; // yes please
	private final boolean cream;
	
	public Coffee(boolean cream, boolean sugar) {
		this.cream = cream;
		this.sugar = sugar;
	}
}