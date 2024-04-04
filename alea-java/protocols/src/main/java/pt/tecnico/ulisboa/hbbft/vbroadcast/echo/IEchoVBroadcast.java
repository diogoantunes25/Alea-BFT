package pt.tecnico.ulisboa.hbbft.vbroadcast.echo;

import pt.tecnico.ulisboa.hbbft.Step;
import pt.tecnico.ulisboa.hbbft.vbroadcast.IVBroadcast;
import pt.tecnico.ulisboa.hbbft.vbroadcast.VOutput;
import pt.tecnico.ulisboa.hbbft.vbroadcast.echo.messages.EchoMessage;
import pt.tecnico.ulisboa.hbbft.vbroadcast.echo.messages.FinalMessage;
import pt.tecnico.ulisboa.hbbft.vbroadcast.echo.messages.SendMessage;

public interface IEchoVBroadcast extends IVBroadcast {
    Step<VOutput> handleSendMessage(SendMessage sendMessage);
    Step<VOutput> handleEchoMessage(EchoMessage echoMessage);
    Step<VOutput> handleFinalMessage(FinalMessage finalMessage);
}
