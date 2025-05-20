package guru.qa.niffler.jupiter.extention;

import guru.qa.niffler.data.tpl.Connections;

public class DataBasesExtension implements SuiteExtension {
    @Override
    public void afterSuite() {
        Connections.closeAllConnections();
    }
}
