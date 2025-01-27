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
  name: '매출',
  data: props.data.map(d => d.value)
}])

const chartOptions = {
  chart: {
    type: 'line',
    toolbar: {
      show: false
    },
    animations: {
      enabled: true,
      easing: 'linear',
      speed: 500
    }
  },
  stroke: {
    curve: 'smooth',
    width: 3
  },
  colors: ['#4F46E5'],
  grid: {
    borderColor: '#f1f1f1',
    strokeDashArray: 4,
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
  xaxis: {
    categories: props.data.map(d => d.date),
    labels: {
      style: {
        fontSize: '12px',
        fontFamily: 'inherit'
      }
    },
    axisBorder: {
      show: false
    },
    axisTicks: {
      show: false
    }
  },
  yaxis: {
    labels: {
      style: {
        fontSize: '12px',
        fontFamily: 'inherit'
      },
      formatter: value => value.toLocaleString() + '원'
    }
  },
  tooltip: {
    x: {
      show: true
    },
    y: {
      formatter: value => value.toLocaleString() + '원'
    }
  }
}
</script>