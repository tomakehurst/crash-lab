import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.junit.rules.RuleChain;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

public class MyTestEnvironment implements TestRule {

    private final RuleChain ruleChain =
            RuleChain.outerRule(new ZookeeperRule())
                     .around(new KafkaRule())
                     .around(new HipDbRule());

    @Override
    public Statement apply(Statement base, Description description) {
        return ruleChain.apply(base, description);
    }
}
