package parafulmine;

import java.util.logging.Logger;

import org.bukkit.event.weather.WeatherListener;

public class PFWeatherListener extends WeatherListener{
    
	public static ParaFulmine plugin;
    
    public PFWeatherListener(ParaFulmine instance) {
            plugin = instance;
    }
    Logger log = Logger.getLogger("Minecraft");
}
