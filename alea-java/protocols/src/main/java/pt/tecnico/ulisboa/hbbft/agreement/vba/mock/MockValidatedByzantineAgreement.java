package pt.tecnico.ulisboa.hbbft.agreement.vba.mock;

import pt.tecnico.ulisboa.hbbft.Step;
import pt.tecnico.ulisboa.hbbft.agreement.vba.IValidatedBinaryAgreement;
import pt.tecnico.ulisboa.hbbft.agreement.vba.ValidatedBinaryAgreementMessage;
import pt.tecnico.ulisboa.hbbft.agreement.vba.ValidatedBoolean;

import java.util.Optional;

public class MockValidatedByzantineAgreement implements IValidatedBinaryAgreement {

    private final String pid;
    private final Long target;
    private final ValidatedBoolean result;

    private Long invocations = 0L;
    private ValidatedBoolean output;

    public MockValidatedByzantineAgreement(String pid, Long target) {
        this(pid, target, new ValidatedBoolean(true, new byte[0]));
    }

    public MockValidatedByzantineAgreement(String pid, Long target, ValidatedBoolean result) {
        this.pid = pid;
        this.target = target;
        this.result = result;
    }

    @Override
    public String getPid() {
        return pid;
    }

    @Override
    public Step<ValidatedBoolean> handleInput(ValidatedBoolean input) {
        return this.tryOutput();
    }

    @Override
    public Step<ValidatedBoolean> handleMessage(ValidatedBinaryAgreementMessage message) {
        return this.tryOutput();
    }

    @Override
    public boolean hasTerminated() {
        return output != null;
    }

    @Override
    public Optional<ValidatedBoolean> deliver() {
        return Optional.empty();
    }

    private Step<ValidatedBoolean> tryOutput() {
        Step<ValidatedBoolean> step = new Step<>();

        this.invocations++;
        if (this.invocations.equals(target)) {
            this.output = this.result;
            step.add(this.output);
        }

        return step;
    }
}
