using Newtonsoft.Json;


namespace Weather.Models
{
    public class ForecastData
    {
        [JsonProperty("list")]
        public ForecastEntry[] Entries { get; set; }
    }

    public class ForecastEntry
    {
        [JsonProperty("dt")]
        public string DateTime { get; set; }

        [JsonProperty("weather")]
        public WeatherStatus[] Weather { get; set; }

        [JsonProperty("main")]
        public Main Main { get; set; }
    }
}
