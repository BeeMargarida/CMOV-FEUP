using System;
using Xamarin.Forms;
using Xamarin.Forms.Xaml;
using SkiaSharp.Views.Forms;
using Weather.Models;
using Weather.Utils;
using System.Collections.Generic;
using Weather.Themes;

namespace Weather
{
    [XamlCompilation(XamlCompilationOptions.Compile)]
    public partial class SingleDayPage : ContentPage
    {
        private City city;
        private String day;
        public SingleDayPage(City city, String day)
        {
            this.city = city;
            this.day = day;

            if (this.day.Equals("Today"))
                this.BindingContext = city.Today;
            else if (this.day.Equals("Tomorrow"))
                this.BindingContext = city.Tomorrow;
               
            this.DrawGraph();
            this.changeTheme();

            InitializeComponent();
        }

        protected override async void OnAppearing()
        {
            base.OnAppearing();
            changeTheme();
        }

        private void changeTheme()
        {

            ICollection<ResourceDictionary> mergedDictionaries = Application.Current.Resources.MergedDictionaries;
            if (mergedDictionaries != null)
            {
                mergedDictionaries.Clear();
                //TODO: Ter em conta o estado do tempo em vez da cidade
                switch (this.city.Name)
                {
                    case "Porto":
                        mergedDictionaries.Add(new SunnyTheme());
                        break;
                    case "Lisbon":
                        mergedDictionaries.Add(new CloudyTheme());
                        break;
                    case "Faro":
                        mergedDictionaries.Add(new RainyTheme());
                        break;
                    default:
                        mergedDictionaries.Add(new BasicTheme());
                        break;
                }
            }
        }

        private void DrawGraph()
        {
            Device.BeginInvokeOnMainThread(() =>
            {
                weatherGraph.InvalidateSurface();
            });
        }

        private void GraphPaintSurface(object sender, SKPaintSurfaceEventArgs e)
        {
            if (this.day.Equals("Today"))
                Graph.Draw(e, city.Today);
            else if (this.day.Equals("Tomorrow"))
                Graph.Draw(e, city.Tomorrow);
        }

        private void onGraphTapped(object sender, EventArgs args)
        {
            //TODO: See if this is needed
            (sender as SKCanvasView).InvalidateSurface();
        }

        private void OnGraphRightSwiped(object sender, EventArgs args)
        {
            //TODO: See if this is needed
            (sender as SKCanvasView).InvalidateSurface();
        }

        private void OnGraphLeftSwiped(object sender, EventArgs args)
        {
            //TODO: See if this is needed
            (sender as SKCanvasView).InvalidateSurface();
        }
    }
}