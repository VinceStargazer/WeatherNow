package vince.stargazer.weathernow;

public class WeatherRVModal {
    private final String time, temperature, iconURL, windSpeed;

    public WeatherRVModal(String time, String temperature, String iconURL, String windSpeed) {
        this.time = time;
        this.temperature = temperature;
        this.iconURL = iconURL;
        this.windSpeed = windSpeed;
    }

    public String getTime() {
        return time;
    }

    public String getTemperature() {
        return temperature;
    }

    public String getIconURL() {
        return iconURL;
    }

    public String getWindSpeed() {
        return windSpeed;
    }
}
