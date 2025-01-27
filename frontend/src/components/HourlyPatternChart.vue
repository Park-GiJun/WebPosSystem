<!-- HourlyPatternChart.vue -->
<template>
  <div class="w-full h-full">
    <ApexChart
        type="line"
        :options="chartOptions"
        :series="series"
        height="100%"
    />
  </div>
</template>

<script setup>
import { computed } from 'vue'
import ApexChart from 'vue3-apexcharts'

const props = defineProps({
  data: {
    type: Array,
    required: true,
    default: () => []
  }
})

const series = computed(() => [{
  name: '결제건수',
  data: props.data.map(d => d.count)
}])

const chartOptions = {
  chart: {
    type: 'line',
    toolbar: {
      show: false
    }
  },
  stroke: {
    curve: 'smooth',
    width: 3
  },
  markers: {
    size: 4
  },
  colors: ['#4F46E5'],
  grid: {
    borderColor: '#f1f1f1',
    strokeDashArray: 4
  },
  xaxis: {
    categories: props.data.map(d => d.hour),
    labels: {
      style: {
        fontSize: '12px',
        fontFamily: 'inherit'
      }
    }
  },
  yaxis: {
    labels: {
      formatter: value => value.toLocaleString() + '건'
    }
  },
  tooltip: {
    y: {
      formatter: value => value.toLocaleString() + '건'
    }
  }
}
</script>