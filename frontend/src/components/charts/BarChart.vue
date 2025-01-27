<!-- components/charts/BarChart.vue -->
<template>
  <div class="chart-container">
    <VueApexCharts
        type="bar"
        height="100%"
        :options="chartOptions"
        :series="series"
    />
  </div>
</template>

<script setup>
import { computed } from 'vue'
import VueApexCharts from 'vue3-apexcharts'

const props = defineProps({
  data: {
    type: Array,
    required: true
  },
  xKey: {
    type: String,
    default: 'category'
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
    }
  },
  colors: [props.color],
  plotOptions: {
    bar: {
      borderRadius: 4,
      columnWidth: '60%'
    }
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
  tooltip: {
    y: {
      formatter: value => value.toLocaleString()
    }
  }
}))
</script>