
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

import Flyweight.FlyweightTest;
import Proxy.ProxyTest;

@Suite
@SelectClasses({FlyweightTest.class, ProxyTest.class})
public class CompleteUnitTest {

}
