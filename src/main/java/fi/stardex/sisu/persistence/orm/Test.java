package fi.stardex.sisu.persistence.orm;

import fi.stardex.sisu.persistence.orm.cr.inj.TestName;

public interface Test {
    default TestName getTestName() {
        return null;
    }
}
