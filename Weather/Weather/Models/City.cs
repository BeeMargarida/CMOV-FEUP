using System;
using Xamarin.Forms;

namespace Weather.Models
{
    public class City
    {
        public string Name { get; set; }
        public long ID { get; set; }
        public string CurrentWeatherStatus { get; set; }
        public ImageSource CurrentWeatherIcon { get; set; }
        public long Visibility { get; set; }
        public double WindSpeed { get; set; }
        public long WindDegree { get; set; }
        public string Temperature { get; set; }
        public long Pressure { get; set; }
        public long Humidity { get; set; }
        public string MinTemperature { get; set; }
        public string MaxTemperature { get; set; }
        public ForecastData Forecast { get; set; }

        public City(string name)
        {
            this.Name = name;
        }

        public void setData(ResultData data)
        {
            this.Name = data.Name;
            this.ID = data.ID;
            this.CurrentWeatherStatus = data.Weather[0].Status;
            this.CurrentWeatherIcon = new Uri("http://openweathermap.org/img/wn/" + data.Weather[0].Icon + "@2x.png");
            this.Visibility = data.Visibility;
            this.WindSpeed = data.Wind.Speed;
            this.WindDegree = data.Wind.Degree;
            this.Temperature = Math.Round(data.Main.Temperature).ToString();
            this.Pressure = data.Main.Pressure;
            this.Humidity = data.Main.Humidity;
            this.MinTemperature = Math.Round(data.Main.MinTemperature).ToString();
            this.MaxTemperature = Math.Round(data.Main.MaxTemperature).ToString();
        }

    }
}
