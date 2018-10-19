package fi.stardex.sisu.pumps.scv;



import fi.stardex.sisu.pumps.Command;

import java.util.List;


public interface SCV extends Command {

    List<String> obtainLines();

}
