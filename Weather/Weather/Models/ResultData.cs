using Newtonsoft.Json;

namespace Weather.Utils
{
    public class ResultData
    {
        [JsonProperty("name")]
        public string Name { get; set; }

        [JsonProperty("id")]
        public long ID { get; set; }

        [JsonProperty("weather")]
        public WeatherStatus[] Weather { get; set; }

        [JsonProperty("main")]
        public Main Main { get; set; }

        [JsonProperty("visibility")]
        public long Visibility { get; set; }

        [JsonProperty("wind")]
        public Wind Wind { get; set; }
    }

    public class Main
    {
        [JsonProperty("temp")]
        public double Temperature { get; set; }

        [JsonProperty("pressure")]
        public long Pressure { get; set; }

        [JsonProperty("humidity")]
        public long Humidity { get; set; }

        [JsonProperty("temp_min")]
        public double MinTemperature { get; set; }

        [JsonProperty("temp_max")]
        public double MaxTemperature { get; set; }
    }

    public class WeatherStatus
    {
        [JsonProperty("id")]
        public long ID { get; set; }

        [JsonProperty("main")]
        public string Status { get; set; }

        [JsonProperty("description")]
        public string StatusDescription { get; set; }

        [JsonProperty("icon")]
        public string Icon { get; set; }
    }

    public class Wind
    {
        [JsonProperty("speed")]
        public double Speed { get; set; }

        [JsonProperty("degree")]
        public long Degree { get; set; }
    }
}
