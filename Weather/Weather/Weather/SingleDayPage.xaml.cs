using System;
using Xamarin.Forms;
using Xamarin.Forms.Xaml;
using SkiaSharp.Views.Forms;
using Weather.Models;
using Weather.Utils;

namespace Weather
{
    [XamlCompilation(XamlCompilationOptions.Compile)]
    public partial class SingleDayPage : ContentPage
    {
        //TODO: DELETE, IT'S JUST FOR PREVIEW
        public SingleDayPage()
        {
            InitializeComponent();
        }

        private City city;
        public SingleDayPage(City city, String day)
        {
            InitializeComponent();
            this.city = city;

            if(day.Equals("Today"))
                this.BindingContext = city.Today;
            else if(day.Equals("Tomorrow"))
                this.BindingContext = city.Tomorrow;

            this.DrawGraph();
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
            Graph.Draw(e, city.Today);
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