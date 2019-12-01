using Weather.Utils;

namespace Weather.Models
{
    public class City
    {
        public string Name { get; set; }
        public long ID { get; set; }
        public string CurrentWeatherStatus { get; set; }
        public string CurrentWeatherIcon { get; set; }
        public long Visibility { get; set; }
        public double WindSpeed{ get; set; }
        public long WindDegree { get; set; }
        public double Temperature { get; set; }
        public long Pressure { get; set; }
        public long Humidity { get; set; }
        public double MinTemperature { get; set; }
        public double MaxTemperature { get; set; }

        public City(string name)
        {
            this.Name = name;
        }

        public void setData(ResultData data)
        {
            this.Name = data.Name;
            this.ID = data.ID;
            this.CurrentWeatherStatus = data.Weather[0].Status;
            this.CurrentWeatherIcon = data.Weather[0].Icon;
            this.Visibility = data.Visibility;
            this.WindSpeed = data.Wind.Speed;
            this.WindDegree = data.Wind.Degree;
            this.Temperature = data.Main.Temperature;
            this.Pressure = data.Main.Pressure;
            this.Humidity = data.Main.Humidity;
            this.MinTemperature = data.Main.MinTemperature;
            this.MaxTemperature = data.Main.MaxTemperature;
        }

    }
}
