using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Linq;
using Xamarin.Essentials;
using Xamarin.Forms;
using Weather.Models;
using Weather.Utils;
using System.ComponentModel;

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
                            city.setData(data);

                            Cities.Add(city);
                        }
                       
                    }
                }
            }
            catch (Exception e)
            {
                await Application.Current.MainPage.DisplayAlert(
                    "An error occurred",
                    e.Message,
                    "Ok"
                );
            }
            finally
            {
                IsBusy = false;
                listView.ItemsSource = Cities.ToList();
            } 
        }

        async void onSearchButtonPressed(object sender, EventArgs e)
        {

            SearchBar searchBar = (SearchBar)sender;

            if (searchBar.Text != "")
            {
                ResultData data = await Client.getCurrentWeather(Client.generateUri("weather", searchBar.Text));

                if (data == null)
                {
                    Debug.WriteLine("\tERROR {0}", "City doesn't exist");
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
                await Navigation.PushAsync(new CityPage
                {
                    BindingContext = e.SelectedItem as City
                });
            }
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
}