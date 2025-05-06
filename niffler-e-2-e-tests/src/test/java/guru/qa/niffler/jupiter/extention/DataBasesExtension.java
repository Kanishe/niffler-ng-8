package guru.qa.niffler.jupiter.extention;

import guru.qa.niffler.data.DataBases;

public class DataBasesExtension implements SuiteExtension {
    @Override
    public void afterSuite() {
        DataBases.closeAllConnections();
    }
}
