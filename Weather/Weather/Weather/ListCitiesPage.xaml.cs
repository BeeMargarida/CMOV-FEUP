using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Linq;
using Xamarin.Essentials;
using Xamarin.Forms;
using Weather.Models;
using Weather.Utils;
using System.ComponentModel;
using Weather.Themes;

namespace Weather
{
    public partial class ListCitiesPage : ContentPage, INotifyPropertyChanged
    {

        HttpService Client;
        public List<City> Cities;
        private bool IsBusy;
        public ListCitiesPage()
        {
          
            InitializeComponent();

            IsBusy = false;
            Client = new HttpService();
            Cities = new List<City>();
        }

        protected override async void OnAppearing()
        {
            base.OnAppearing();

            if (IsBusy)
                return;
            try
            {
                IsBusy = true;
                Cities = new List<City>();

                var stringCities = Preferences.Get("cities", "");
                if (stringCities != "")
                {
                    string[] split = stringCities.Split(':');
                    foreach (var word in split)
                    {
                        if(word != "")
                        {
                            City city = new City(word);
                            ResultData data = await Client.getCurrentWeather(Client.generateUri("weather", word));

                            if(data == null)
                            {
                                throw new Exception();
                            }
                            city.setData(data);

                            Cities.Add(city);
                        }
                       
                    }
                }
            }
            catch (Exception e)
            {
                await Application.Current.MainPage.DisplayAlert(
                    "An error occurred, please try again later.",
                    e.Message,
                    "Ok"
                );
            }
            finally
            {
                IsBusy = false;
                listView.ItemsSource = Cities.ToList();
            }
            changeTheme();
        }

        private void changeTheme()
        {
            ICollection<ResourceDictionary> mergedDictionaries = Application.Current.Resources.MergedDictionaries;
            if (mergedDictionaries != null)
            {
                mergedDictionaries.Clear();
                mergedDictionaries.Add(new BasicTheme());
            }
        }

        async void onSearchButtonPressed(object sender, EventArgs e)
        {

            SearchBar searchBar = (SearchBar)sender;

            if (searchBar.Text != "")
            {
                foreach (City c in this.Cities)
                {
                    if (c.Name.ToLower() == searchBar.Text.ToLower())
                    {
                        await Application.Current.MainPage.DisplayAlert(
                            "Repeated City",
                            "City " + searchBar.Text + " is already in your list.",
                            "Ok"
                        );
                        return;
                    }
                }

                ResultData data = await Client.getCurrentWeather(Client.generateUri("weather", searchBar.Text));

                if (data == null)
                {
                    await Application.Current.MainPage.DisplayAlert(
                        "Not Valid",
                        "City " + searchBar.Text + " does not exist",
                        "Ok"
                    );
                }
                else
                {
                    // Save new city data
                    City city = new City(searchBar.Text);
                    city.setData(data);
                    Cities.Insert(0,city);

                    saveCities();

                    searchBar.Text = "";
                    listView.ItemsSource = Cities.ToList();
                }
            }
        }

        async void OnListViewItemSelected(object sender, SelectedItemChangedEventArgs e)
        {

            if (e.SelectedItem != null)
            {
                ((ListView)sender).SelectedItem = null;
                await Navigation.PushAsync(new CityPage((City) e.SelectedItem));
            }

        }

        async void OnDeleteButtonClicked(object sender, EventArgs args)
        {
            Debug.WriteLine("INFO: " + (sender as Button).CommandParameter.ToString());
            string name = (sender as Button).CommandParameter.ToString();
            foreach(City city in this.Cities)
            {
                if(city.Name == name)
                {
                    this.Cities.Remove(city);
                    break;
                }
            }
            listView.ItemsSource = this.Cities.ToList();
            this.saveCities();
        }

        void saveCities()
        {
            string citiesString = "";
            foreach (City city in Cities)
            {
                citiesString += city.Name + ":";
            }
            Preferences.Set("cities", citiesString);

        }
    }

    public class NegateBooleanConverter : IValueConverter
    {
        public object Convert(object value, Type targetType, object parameter, System.Globalization.CultureInfo culture)
        {
            return !(bool)value;
        }
        public object ConvertBack(object value, Type targetType, object parameter, System.Globalization.CultureInfo culture)
        {
            return !(bool)value;
        }
    }
}