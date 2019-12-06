using System;
using Xamarin.Forms;
using Xamarin.Forms.Xaml;
using SkiaSharp.Views.Forms;
using Weather.Models;
using Weather.Utils;

namespace Weather
{
    [XamlCompilation(XamlCompilationOptions.Compile)]
    public partial class TomorrowPage : ContentPage
    {
        private City city;
        public TomorrowPage(City city)
        {
            this.city = city;
            InitializeComponent();
            this.BindingContext = this.city.Tomorrow;
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