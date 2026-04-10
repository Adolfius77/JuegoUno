package factorys;

import broker.EnrutadorBroker;

public class enrutadorBrokerFactory {
    public static EnrutadorBroker CrearEnrutadorBroker() {
        return new EnrutadorBroker();
    }
}
