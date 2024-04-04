package pt.tecnico.ulisboa.hbbft.agreement.mvba;

import pt.tecnico.ulisboa.hbbft.agreement.vba.ValidatedBoolean;
import pt.tecnico.ulisboa.hbbft.agreement.vba.Validator;

public class VcbcValidator implements Validator {
    @Override
    public boolean validate(ValidatedBoolean value) {
        return true;
    }
}
