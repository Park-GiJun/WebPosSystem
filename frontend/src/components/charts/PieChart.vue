<!-- components/charts/PieChart.vue -->
<template>
  <div class="chart-container">
    <VueApexCharts
        type="donut"
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
  nameKey: {
    type: String,
    default: 'name'
  },
  valueKey: {
    type: String,
    default: 'value'
  }
})

const series = computed(() =>
    props.data.map(item => item[props.valueKey])
)

const chartOptions = computed(() => ({
  chart: {
    toolbar: {
      show: false
    }
  },
  colors: ['#3B82F6', '#10B981', '#F59E0B', '#EF4444', '#8B5CF6', '#EC4899'],
  labels: props.data.map(item => item[props.nameKey]),
  plotOptions: {
    pie: {
      donut: {
        size: '60%'
      }
    }
  },
  dataLabels: {
    enabled: false
  },
  legend: {
    position: 'bottom',
    markers: {
      radius: 12
    },
    itemMargin: {
      horizontal: 15,
      vertical: 8
    }
  },
  tooltip: {
    y: {
      formatter: (value) => {
        const total = series.value.reduce((a, b) => a + b, 0)
        const percentage = ((value * 100) / total).toFixed(1)
        return `${percentage}% (${value.toLocaleString()})`
      }
    }
  }
}))
</script>

<style scoped>
.chart-container {
  width: 100%;
  height: 100%;
}
</style>