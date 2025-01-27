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
  data: props.data.map(d => d.sales)
}])

const chartOptions = {
  chart: {
    type: 'line',
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
    size: 4,
    colors: ['#4F46E5'],
    strokeColors: '#fff',
    strokeWidth: 2,
    hover: {
      size: 6
    }
  },
  xaxis: {
    categories: props.data.map(d => d.hour),
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
  },
  theme: {
    mode: 'light'
  }
}
</script>