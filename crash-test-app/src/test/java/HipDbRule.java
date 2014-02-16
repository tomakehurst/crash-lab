import com.yammer.dropwizard.scopes.flash.HipDb;
import org.junit.rules.ExternalResource;

public class HipDbRule extends ExternalResource {

    private HipDb db = new HipDb();

    @Override
    protected void before() throws Throwable {
        db.init();
        db.start();
    }

    @Override
    protected void after() {
        db.shutdown();
    }
}
