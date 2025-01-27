<!-- PaymentTrendChart.vue -->
<template>
  <div class="w-full h-full">
    <ApexChart
        type="area"
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
  name: '결제금액',
  data: props.data.map(d => d.amount)
}])

const chartOptions = {
  chart: {
    type: 'area',
    toolbar: {
      show: false
    },
    animations: {
      enabled: true,
      easing: 'smooth',
      speed: 800
    }
  },
  stroke: {
    curve: 'smooth',
    width: 3
  },
  fill: {
    type: 'gradient',
    gradient: {
      shadeIntensity: 1,
      opacityFrom: 0.7,
      opacityTo: 0.2,
      stops: [0, 90, 100]
    }
  },
  colors: ['#4F46E5'],
  grid: {
    borderColor: '#f1f1f1',
    strokeDashArray: 4
  },
  markers: {
    size: 4,
    colors: ['#4F46E5'],
    strokeColors: '#fff',
    strokeWidth: 2
  },
  xaxis: {
    categories: props.data.map(d => d.date),
    labels: {
      style: {
        fontSize: '12px',
        fontFamily: 'inherit'
      }
    }
  },
  yaxis: {
    labels: {
      formatter: value => value.toLocaleString() + '원'
    }
  },
  tooltip: {
    y: {
      formatter: value => value.toLocaleString() + '원'
    }
  }
}
</script>