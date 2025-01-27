<!-- PaymentChart.vue -->
<template>
  <div class="w-full h-full">
    <ApexChart
        type="bar"
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
  data: props.data.map(d => d.value)
}])

const chartOptions = {
  chart: {
    type: 'bar',
    background: 'transparent',
    toolbar: {
      show: false
    },
    animations: {
      enabled: true,
      dynamicAnimation: {
        speed: 350
      }
    }
  },
  plotOptions: {
    bar: {
      borderRadius: 6,
      columnWidth: '60%',
      dataLabels: {
        position: 'top'
      }
    }
  },
  colors: ['#4F46E5'],
  dataLabels: {
    enabled: true,
    formatter: val => val.toLocaleString(),
    offsetY: -20,
    style: {
      fontSize: '12px',
      fontFamily: 'inherit'
    }
  },
  grid: {
    show: false
  },
  xaxis: {
    categories: props.data.map(d => d.name),
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
    show: false
  },
  tooltip: {
    y: {
      formatter: value => value.toLocaleString()
    }
  }
}
</script>