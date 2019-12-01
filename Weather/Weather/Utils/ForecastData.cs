using Newtonsoft.Json;


namespace Weather.Utils
{
    class ForecastData
    {
        [JsonProperty("list")]
        public ForecastEntry[] Entries { get; set; }
    }

    class ForecastEntry
    {
        [JsonProperty("dt")]
        public string DateTime { get; set; }

        [JsonProperty("weather")]
        public WeatherStatus[] Weather { get; set; }

        [JsonProperty("main")]
        public Main Main { get; set; }
    }
}
