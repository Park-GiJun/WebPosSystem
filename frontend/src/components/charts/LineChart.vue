<template>
  <div class="chart-container">
    <VueApexCharts
        type="line"
        height="100%"
        :options="chartOptions"
        :series="series"
    />
  </div>
</template>

<script setup>
import {computed} from 'vue'
import VueApexCharts from 'vue3-apexcharts'

const props = defineProps({
  data: {
    type: Array,
    required: true
  },
  xKey: {
    type: String,
    default: 'time'
  },
  yKey: {
    type: String,
    default: 'value'
  },
  color: {
    type: String,
    default: '#3B82F6'
  }
})

const series = computed(() => [{
  name: '',
  data: props.data.map(item => item[props.yKey])
}])

const chartOptions = computed(() => ({
  chart: {
    toolbar: {
      show: false
    },
    zoom: {
      enabled: false
    }
  },
  colors: [props.color],
  stroke: {
    curve: 'smooth',
    width: 3
  },
  xaxis: {
    categories: props.data.map(item => item[props.xKey]),
    axisBorder: {
      show: false
    },
    axisTicks: {
      show: false
    }
  },
  yaxis: {
    labels: {
      formatter: value => value.toLocaleString()
    }
  },
  grid: {
    borderColor: '#E5E7EB',
    xaxis: {
      lines: {
        show: true
      }
    }
  },
  markers: {
    size: 5,
    hover: {
      size: 7
    }
  },
  tooltip: {
    y: {
      formatter: value => value.toLocaleString()
    }
  }
}))
</script>