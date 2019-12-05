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

        [JsonProperty("dt_txt")]
        public string DateTimeText { get; set; }

        [JsonProperty("weather")]
        public WeatherStatus[] Weather { get; set; }

        [JsonProperty("main")]
        public Main Main { get; set; }

        [JsonProperty("wind")]
        public Wind Wind { get; set; }

        [JsonProperty("rain")]
        public Rain Rain { get; set; }
    }
}
